package com.test;

import java.util.List;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {
    private PostDao postDao;
    @Autowired
    IServiceHi iServiceHi;

    public Query(PostDao postDao) {
        this.postDao = postDao;
    }

    public List<Post> recentPosts(int count, int offset) {
        return postDao.getRecentPosts(count, offset);
    }

    public List<Post> auPosts(String authorId) {
        return postDao.getAuthorPosts(authorId);
    }

    public Message getService1Str(String name) {
        String rlt = iServiceHi.sayHiFromClientOne(name);
        Message msg = new Message();
        msg.setId("1");
        msg.setName(rlt);
        return msg;
    }
}
