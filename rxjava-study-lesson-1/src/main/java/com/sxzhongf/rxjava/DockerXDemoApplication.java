package com.sxzhongf.rxjava;

import com.sxzhongf.rxjava.publisher.impl.DockerXDemoPublisher;
import com.sxzhongf.rxjava.subscriber.impl.DockerXDemoSubscriber;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * DockerXDemoApplication for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/7/7
 */
public class DockerXDemoApplication {

    public static void main(String[] args) {
        flowCustomSubmissionPublisher();
    }

    /**
     * 订阅演示
     *
     * @param publisher
     * @param subscribeName
     */
    private static void demoSubscribe(DockerXDemoPublisher<Integer> publisher
            , String subscribeName) {
        DockerXDemoSubscriber<Integer> subscriber =
                new DockerXDemoSubscriber<>(4L, subscribeName);
        publisher.subscribe(subscriber);
    }

    public static void flowCustomSubmissionPublisher() {
        //Executors.newFixedThreadPool(3);
        ExecutorService executor = ForkJoinPool.commonPool();
        try (DockerXDemoPublisher<Integer> publisher = new DockerXDemoPublisher<>(executor)) {
            demoSubscribe(publisher, "one");
            demoSubscribe(publisher, "two");
            demoSubscribe(publisher, "three");
            IntStream.range(1, 5).forEach(publisher::submit);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                executor.shutdown();
                int shutDownDelaySecond = 1;
                System.out.println("...........等待 " + shutDownDelaySecond +
                        " 秒后结束服务。");
                executor.awaitTermination(shutDownDelaySecond, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("调用executor.shutdownNow()结束服务...");
                List<Runnable> list = executor.shutdownNow();
                System.out.println("还剩 " + list.size() + " 个任务等待执行，服务已关闭。");
            }
        }
    }
}
