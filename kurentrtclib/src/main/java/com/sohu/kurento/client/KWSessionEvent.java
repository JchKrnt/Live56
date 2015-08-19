package com.sohu.kurento.client;

/**
 * Created by jingbiaowang on 2015/7/24.
 */
public interface KWSessionEvent {

    void createOffer();

    void processAnwser(String anwser);

    /**
     * stop.
     */
    public void dispose();

}
