package com.elasticsearch.demo.Service;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.elasticsearch.demo.pojo.Book;

/**
 * @author lixing
 */
@Service
@Transactional
public interface BookRepository extends ElasticsearchRepository<Book, Integer> {
}
