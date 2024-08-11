package me.jungyeeun.springbootdeveloper.repository;

import me.jungyeeun.springbootdeveloper.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
