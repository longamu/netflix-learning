package com.yourbatman.hystrix;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestRxJava {

    @Test
    public void fun1() {
        // 自定义一个线程池：用于处理消费者任务
        ExecutorService myDiyThreadExe = Executors.newFixedThreadPool(1, r -> {
            Thread thread = new Thread(r);
            thread.setName("myDiyThread");
            return thread;
        });

        // Observable.just(1, 2, 3, 4, 5, 6)
        Observable.from(new Integer[]{1, 2, 3, 4, 5, 6})
                .subscribeOn(Schedulers.io()) //（发送事件的线程所在地，只能生效一次）
                .observeOn(Schedulers.immediate()) // 设置下面的Map操作，在当前线程立马执行（可生效多次）
                .map(i -> {
                    System.out.println("map操作执行线程：" + Thread.currentThread().getName());
                    return i * i;
                })
                .observeOn(Schedulers.newThread()) // 因为这是新线程，所以控制台的日志打印换乱串~~~
                .filter(i -> {
                    System.out.println("filter操作执行线程：" + Thread.currentThread().getName());
                    return i > 10;
                })

                .observeOn(Schedulers.from(myDiyThreadExe)) // 任务在自定义的线程池里执行
                // 处理事件：订阅：使用Action处理
                .subscribe(i -> System.out.printf("subscribe订阅处理线程 %s，值为%s \n", Thread.currentThread().getName(), i));

        // hold主线程
        while (true) {
        }

    }

    @Test
    public void fun2() {
        // 三种执行方式：

        // 1、普通方式
        String s = new CommandHelloWorld("Bob").execute();
        System.out.println(s);

        String fallbackValue = new CommandHelloWorld(null).execute();
        // 说明：若你没有提供fallback函数，那结果是：
        // com.netflix.hystrix.exception.HystrixRuntimeException: CommandHelloWorld failed and no fallback available.
        System.out.println(fallbackValue);


        // 2、异步方式。什么时候需要时候什么时候get
        // Future<String> s = new CommandHelloWorld("Bob").queue();
        // System.out.println(s.get());

        // 3、RxJava方式。吞吐量更高，但对程序员的要求更高
        // Observable<String> s = new CommandHelloWorld("Bob").observe();
        // s.subscribe(d -> System.out.println(d));
    }

    @Test
    public void fun3() {
        Observable.just(1, 2, 3, 4)
                .doOnNext((d) -> System.out.println("我是doOnNext" + d))
                .doOnCompleted(() -> System.out.println("我是doOnCompleted"))
                // onErrorResumeNext只有发射数据是抛错了才会执行它。比如这里牛可以fallabck了
                .onErrorResumeNext(Observable.just(6, 7, 8, 9))
                .doOnTerminate(() -> System.out.println("我是doOnTerminate"))
                .doOnUnsubscribe(() -> System.out.println("我是doOnUnsubscribe"))
                .subscribe((d) -> System.out.println(d));

        System.out.println("===============模拟发射数据过程中出现异常===============");

        // 模拟发射数据时发生错误了（若正常执行，效果完全同上）
        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
            subscriber.onStart();
            subscriber.onNext(1);
            subscriber.onNext(2);
            System.out.println(1 / 0);
            subscriber.onNext(3);
            subscriber.onNext(4);
            subscriber.onCompleted(); // 请别忘了告诉结束。否则doOnCompleted不会执行的
        }).doOnNext((d) -> System.out.println("我是doOnNext" + d))
                .doOnCompleted(() -> System.out.println("我是doOnCompleted"))
                .onErrorResumeNext(Observable.just(6, 7, 8, 9))
                .doOnTerminate(() -> System.out.println("我是doOnTerminate"))
                .doOnUnsubscribe(() -> System.out.println("我是doOnUnsubscribe"))
                .subscribe((d) -> System.out.println(d));
    }
}
