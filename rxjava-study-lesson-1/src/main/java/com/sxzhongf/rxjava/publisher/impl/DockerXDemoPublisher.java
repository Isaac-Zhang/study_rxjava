package com.sxzhongf.rxjava.publisher.impl;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

/**
 * DockerXDemoPublisher for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/7/7
 */
public class DockerXDemoPublisher<T> implements Flow.Publisher<T>, AutoCloseable {
    private final ExecutorService executor; //daemon-based
    private CopyOnWriteArrayList<DockerXDemoSubscription> list = new CopyOnWriteArrayList<>();

    public void submit(T item) {
        System.out.println("************开始发布元素 item: " +
                item + "*************");
        list.forEach(e -> {
            e.future = executor.submit(() -> {
                e.subscriber.onNext(item);
            });
        });
    }

    public DockerXDemoPublisher(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void close() throws Exception {
        list.forEach(e -> {
            e.future = executor.submit(() -> {
                e.subscriber.onComplete();
            });
        });
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new DockerXDemoSubscription<>(subscriber, executor));
        list.add(new DockerXDemoSubscription(subscriber, executor));
    }

    /**
     * 自定义实现{@link Flow.Subscription}
     *
     * @param <T>
     */
    static class DockerXDemoSubscription<T> implements Flow.Subscription {
        private final Flow.Subscriber<? super T> subscriber;
        private final ExecutorService executor;
        private Future<?> future;
        private T item;
        private boolean complated;

        public DockerXDemoSubscription(Flow.Subscriber<? super T> subscriber, ExecutorService executor) {
            this.subscriber = subscriber;
            this.executor = executor;
        }

        @Override
        public void request(long n) {
            if (n != 0 && !complated) {
                if (n < 0) {
                    IllegalArgumentException ex = new IllegalArgumentException();
                    executor.execute(() -> subscriber.onError(ex));
                } else {
                    future = executor.submit(() -> {
                        subscriber.onNext(item);
                    });
                }
            } else {
                subscriber.onComplete();
            }
        }

        @Override
        public void cancel() {
            complated = true;
            if (future != null && !future.isCancelled()) {
                this.future.cancel(true);
            }
        }
    }
}
