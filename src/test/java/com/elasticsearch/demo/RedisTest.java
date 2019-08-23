package com.elasticsearch.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.elasticsearch.demo.pojo.Book;
import com.elasticsearch.demo.Service.BookRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void esTest() {
        Book book = new Book();
        book.setId(1);
        book.setBookName("晋升");
        book.setAuthor("ice");
        bookRepository.save(book);
    }


}

