package com.qianfeng.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;


/**
 * Created by Administrator on 2017/9/13 0013.
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport{
//    @Bean
//    public HttpMessageConverter createConve(){
//        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
//        FastJsonConfig config = new FastJsonConfig();
//        //美化
//        config.setSerializerFeatures(SerializerFeature.PrettyFormat);
//        converter.setFastJsonConfig(config);
//        return converter;
//
//    }



    @Override
    public KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder builder = new StringBuilder();
                builder.append(o.getClass().getName());
                builder.append(method.getName());
                for (Object obj:objects){
                    builder.append(obj);
                }
                return builder.toString();
            }
        };
    }
    @Bean
    public RedisTemplate<Object,Object> createTemplate(RedisConnectionFactory factory){
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setConnectionFactory(factory);
        //指定key序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        //指定value序列化方式
        //jackson
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //fastjson
        //template.setValueSerializer(new FastJsonSerializer<Object>(Object.class));
        template.afterPropertiesSet();
        return template;
    }
    @Bean
    public RedisCacheManager createCm(RedisTemplate<Object,Object> createTemplate){
        RedisCacheManager cm = new RedisCacheManager(createTemplate);
        cm.setDefaultExpiration(60);
        return cm;
    }

    class FastJsonSerializer<T> implements RedisSerializer<T>{

        private  Class<T> ct;
        public FastJsonSerializer (Class<T> t2){
            this.ct=t2;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            byte[] bytes = new byte[0];
            try {
                return JSON.toJSONString(t).getBytes("utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            try {
                String s=new String(bytes,"utf-8");
                return  (T) JSON.parseObject(s,ct);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
}
