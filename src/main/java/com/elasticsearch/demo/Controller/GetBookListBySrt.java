package com.elasticsearch.demo.Controller;

import com.elasticsearch.demo.Service.BookRepository;
import com.elasticsearch.demo.pojo.Book;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @author lixing
 */
public class GetBookListBySrt {

    /**
     * esjap类
     */
    @Autowired
    private BookRepository bookRepository;

    /**
     * es工具
     */
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 从es检索数据
     *
     * @param content  搜索关键字
     * @param pageNum  页
     * @param pageSzie 条
     * @return
     */
    public AggregatedPage<Book> getIdeaListBySrt(String content, Integer pageNum, Integer pageSzie) {
        Pageable pageable = PageRequest.of(pageNum, pageSzie);

        //google的色值
        String preTag = "<font color='#dd4b39'>";
        String postTag = "</font>";

        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(matchQuery("author", content)).
                withQuery(matchQuery("bookName", content)).
                withHighlightFields(new HighlightBuilder.Field("author").preTags(preTag).postTags(postTag),
                        new HighlightBuilder.Field("bookName").preTags(preTag).postTags(postTag)).build();
        searchQuery.setPageable(pageable);

        // 不需要高亮直接return ideas
        // AggregatedPage<Idea> ideas = elasticsearchTemplate.queryForPage(searchQuery, Idea.class);

        //高亮字段
        AggregatedPage<Book> books = elasticsearchTemplate.queryForPage(searchQuery, Book.class, new SearchResultMapper() {

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<Book> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    Book book = new Book();
                    //name or memoe
                    HighlightField ideaTitle = searchHit.getHighlightFields().get("ideaTitle");
                    if (ideaTitle != null) {
                        book.setAuthor(ideaTitle.fragments()[0].toString());
                    }
                    HighlightField ideaContent = searchHit.getHighlightFields().get("ideaContent");
                    if (ideaContent != null) {
                        book.setBookName(ideaContent.fragments()[0].toString());
                    }

                    chunk.add(book);
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }
        });
        return books;
    }
}
