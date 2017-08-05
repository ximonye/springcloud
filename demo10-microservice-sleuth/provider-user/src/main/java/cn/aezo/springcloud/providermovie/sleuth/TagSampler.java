package cn.aezo.springcloud.providermovie.sleuth;

import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Span;

/**
 * Created by smalle on 2017/8/5.
 */
public class TagSampler implements Sampler {
    private String tag;

    public TagSampler(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean isSampled(Span span) {
        return span.tags().get(tag) != null;
    }
}
