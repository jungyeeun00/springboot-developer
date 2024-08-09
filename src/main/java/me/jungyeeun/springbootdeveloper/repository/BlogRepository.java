package me.jungyeeun.springbootdeveloper.repository;

import me.jungyeeun.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
