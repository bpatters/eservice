package com.myl.eservice.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.myl.eservice.model.user.impl.elasticsearch.ElasticUser;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by bpatterson on 3/4/15.
 */
@Configuration
public class CommonBeans {
    @Bean
    UserIndexConfig userIndexConfig() {
        return new UserIndexConfig ("base", "user", ElasticUser.class);
    }

    @Bean(name = "objectMapper")
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JodaModule());
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            // borrowed from: http://jackson-users.ning.com/forum/topics/how-to-not-include-type-info-during-serialization-with
            @Override
            protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {

                // Don't serialize JsonTypeInfo Property includes
                if (ann.hasAnnotation(JsonTypeInfo.class)
                        && ann.getAnnotation(JsonTypeInfo.class).include() == JsonTypeInfo.As.PROPERTY
                        && SerializationConfig.class.isAssignableFrom(config.getClass())) {
                    return null;

                }

                return super._findTypeResolver(config, ann, baseType);
            }
        });

        return mapper;
    }


    @Bean(name ="messages")
    public ResourceBundleMessageSource messages() {
        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();

        messages.setBasename("messages/messages");

        return messages;
    }

    @Bean(name = "appSearchElasticNode", destroyMethod = "close")
    public Node getElasticSearchNode(
            @Value("${elastic.hosts}") String hosts,
            @Value("${elastic.cluster.name}") String clusterName,
            @Value("${elastic.node.local}") boolean localMode) {

        NodeBuilder builder = nodeBuilder().clusterName(clusterName);

        if (localMode) {
            builder.local(true);
        } else {
            builder.settings(
                    ImmutableSettings.builder()
                            .put("discovery.zen.ping.unicast.hosts", hosts)
                            .put("discovery.zen.ping.multicast.enabled", false)
                            .put("node.client", true)
                            .put("node.master", false)
                            .put("node.data", false));
        }

        return builder.node();
    }

    /**
     * Returns a client which may perform operations on an Elastic Search cluster.
     * @see #getElasticSearchNode(String, String, boolean)
     */
    @Bean(name = "appSearchElasticClient", destroyMethod = "close")
    @Autowired
    public Client getElasticClient(Node appSearchElasticNode) {
        return appSearchElasticNode.client();
    }
}
