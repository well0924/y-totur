package com.example.category;

import com.example.category.mapper.CategoryEntityMapper;
import com.example.interfaces.category.CategoryRepositoryPort;
import com.example.model.category.CategoryModel;
import com.example.outbound.category.CategoryOutConnector;
import com.example.rdb.Category;
import com.example.rdb.CategoryRepository;
import com.example.redis.config.CacheConfig;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CategoryOutConnector.findById() 캐싱 및 update/delete 시 evict 정합성 검증.
 * 실제 Redis(TestContainers)를 사용해 Spring Cache AOP가 실제로 동작하는지까지 확인한다.
 *
 * CacheManager를 직접 들여다보는 방식(cacheManager.getCache(...).get(...))은
 * 실행 환경(IDE 러너 vs Gradle)에 따라 결과가 달라지는 경우가 있어 사용하지 않는다.
 * 대신 Mock인 CategoryRepository의 호출 횟수로 "캐시가 실제로 DB 왕복을 막았는지"를
 * 간접 검증한다 — 이 방식이 두 환경 모두에서 일관되게 재현됐다.
 */
@Testcontainers
@SpringBootTest(classes = {
        CategoryOutConnector.class,
        CategoryEntityMapper.class,
        CacheConfig.class,
        CategoryCacheTest.RedisTestConfig.class
})
@ActiveProfiles("test")
class CategoryCacheTest {

    @Container
    static final RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.0"))
            .withExposedPorts(6379);

    // @SpringBootTest(classes=...)로 좁게 로드하면 오토컨피규레이션이 돌지 않아
    // RedisConnectionFactory가 자동 생성되지 않는다 -> 직접 빈으로 정의한다.
    @TestConfiguration
    static class RedisTestConfig {
        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        }
    }

    // CacheConfig의 @EnableCaching은 기본적으로 JDK 동적 프록시를 사용하므로,
    // CategoryOutConnector가 구현한 인터페이스(CategoryRepositoryPort) 타입으로 주입받아야 한다.
    @Autowired
    CategoryRepositoryPort categoryOutConnector;

    @MockBean
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("findById 두 번째 호출은 캐시를 타서 리포지토리를 다시 조회하지 않는다")
    void findById_hitsCacheOnSecondCall() {
        // given
        Category entity = Category.builder().id(1L).name("업무").depth(1L).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));

        // when
        CategoryModel first = categoryOutConnector.findById(1L);
        CategoryModel second = categoryOutConnector.findById(1L);

        // then
        assertThat(first.getName()).isEqualTo("업무");
        assertThat(second.getName()).isEqualTo("업무");
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteCategory 호출 후에는 evict로 인해 findById가 리포지토리를 다시 조회한다")
    void deleteCategory_evictsCache_soNextFindByIdHitsRepositoryAgain() {
        // given
        Category entity = Category.builder().id(3L).name("삭제대상").depth(1L).build();
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(entity));

        // when
        categoryOutConnector.findById(3L);          // 1) 캐시 적재 (repo 호출 1회)
        categoryOutConnector.deleteCategory(3L);     // 2) 내부 조회(repo 호출 1회, 누적 2) + evict
        categoryOutConnector.findById(3L);           // 3) evict 됐다면 캐시 미스 -> repo 호출 1회(누적 3)

        // then: evict가 안 됐다면 3)에서 캐시 히트라 누적 2회에 머물렀을 것이다.
        verify(categoryRepository, times(3)).findById(3L);
    }

    @Test
    @DisplayName("updateCategory 호출 후에는 evict로 인해 findById가 리포지토리를 다시 조회한다")
    void updateCategory_evictsCache_soNextFindByIdHitsRepositoryAgain() {
        // given
        Category entity = Category.builder().id(4L).name("수정전").depth(1L).build();
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(entity));
        when(categoryRepository.save(any(Category.class))).thenReturn(entity);

        // when
        categoryOutConnector.findById(4L);                              // 1) 캐시 적재 (repo 호출 1회)
        categoryOutConnector.updateCategory(4L, "수정후", null, 1L);   // 2) 내부 조회(repo 호출 1회, 누적 2) + evict
        categoryOutConnector.findById(4L);                              // 3) evict 됐다면 캐시 미스 -> repo 호출 1회(누적 3)

        // then
        verify(categoryRepository, times(3)).findById(4L);
    }
}
