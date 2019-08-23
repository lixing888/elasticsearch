package com.elasticsearch.demo.Controller;

import com.elasticsearch.demo.Service.PostRepository;
import com.elasticsearch.demo.pojo.Post;
import org.elasticsearch.index.query.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author lixing
 * http://localhost:8080/esController/multiMatch?title=我是
 */
@RestController
@RequestMapping("/esController")
public class EsController {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private PostRepository postRepository;

    /**
     * 单字符串模糊查询，默认排序。将从所有字段中查找包含传来的word分词后字符串的数据集
     *  http://localhost:8080/esController/singleWord?word=浣溪沙&size=20
     */
    @RequestMapping("/singleWord")
    public Object singleTitle(String word, @PageableDefault Pageable pageable) {
        //使用queryStringQuery完成单字符串查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryStringQuery(word)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 单字符串模糊查询，单字段排序。
     * http://localhost:8080/esController/singleWord1?word=浣溪沙&size=20
     */
    @RequestMapping("/singleWord1")
    public Object singlePost(String word, @PageableDefault(sort = "weight", direction = Sort.Direction.DESC) Pageable pageable) {
        //使用queryStringQuery完成单字符串查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryStringQuery(word)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 单字段对某字符串模糊查询
     * http://localhost:8080/esController/singleMatch?content=落日熔金&size=20
     */
    @RequestMapping("/singleMatch")
    public Object singleMatch(String content, Integer userId, @PageableDefault Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("content", content)).withPageable(pageable).build();
        // SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("userId", userId)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }


    /**
     * 单字段对某短语进行匹配查询，短语分词的顺序会影响结果
     */
    @RequestMapping("/singlePhraseMatch")
    public Object singlePhraseMatch(String content, @PageableDefault Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("content", content)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    @RequestMapping("/add")
    public Object add() {
        Post post = new Post();
        post.setTitle("我是");
        post.setContent("我爱中华人民共和国");
        post.setWeight(1);
        post.setUserId(1);
        postRepository.save(post);
        post = new Post();
        post.setTitle("我是");
        post.setContent("中华共和国");
        post.setWeight(2);
        post.setUserId(2);
        return postRepository.save(post);
    }

    @RequestMapping("/singlePhraseMatch1")
    public Object singlePhraseMatch1(String content, @PageableDefault Pageable pageable) {
        //SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("content", content)).withPageable(pageable).build();
        // 少匹配一个分词也OK、
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("content", content).slop(2)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }


    /**
     * * term匹配，即不分词匹配，你传来什么值就会拿你传的值去做完全匹配
     */
    @RequestMapping("/singleTerm")
    public Object singleTerm(Integer userId, @PageableDefault Pageable pageable) {
        //不对传来的值分词，去找完全匹配的
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("userId", userId)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * 多字段匹配
     */
    @RequestMapping("/multiMatch")
    public Object singleUserId(String title, @PageableDefault(sort = "weight", direction = Sort.Direction.DESC) Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(multiMatchQuery(title, "title", "content")).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * * 单字段包含所有输入(按比例包含)
     */
    @RequestMapping("/contain")
    public Object contain(String title) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", title).operator(Operator.AND).minimumShouldMatch("75%")).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

    /**
     * * 多字段合并查询
     */
    @RequestMapping("/bool")
    public Object bool(String title, Integer userId, Integer weight) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery().must(termQuery("userId", userId)).should(rangeQuery("weight").lt(weight)).must(matchQuery("title", title))).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }

}
