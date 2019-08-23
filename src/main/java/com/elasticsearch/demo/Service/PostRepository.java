package com.elasticsearch.demo.Service;

import com.elasticsearch.demo.pojo.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author lixing
 */
public interface PostRepository extends ElasticsearchRepository<Post, String> {
}
