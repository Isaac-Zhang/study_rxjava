package com.sxzhongf.rxjava.subscriber.impl;

import java.util.concurrent.Flow;

/**
 * DockerXDemoSubscriber for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/7/7
 */
public class DockerXDemoSubscriber<T> implements Flow.Subscriber<T> {
    private String name;
    private Flow.Subscription subscription;
    final long bufferSize;
    long count;

    public String getName() {
        return name;
    }

    public Flow.Subscription getSubscription() {
        return subscription;
    }

    public DockerXDemoSubscriber(long bufferSize, String name) {
        this.bufferSize = bufferSize;
        this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        //count = bufferSize - bufferSize / 2;
        //在消费一半的时候重新请求
        (this.subscription = subscription).request(bufferSize);
        System.out.println("开始onSubscribe订阅");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNext(T item) {
        System.out.println(" ###### " + Thread.currentThread().getName() +
                " name: " + name + " item: " + item + " ###### ");
        System.out.println(name + " received: " + item);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println(Thread.currentThread().getName() + " Complated.");
    }
}
