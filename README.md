总结
===========
* 非常不稳定 pub/sub 模式 30W压测丢了27W条消息，官方没有给出任何的发送状态供业务层处理
* pull/push 模式 限制很死，不能进行双向通信。如果是内网的机子根本无法进行通信。
* rep/req 模式更搞笑，如果接收时对方断开了，玩死自己
* 使用路由代理非常慢 代理 pull/push 300W压测是 20秒 比不用代理慢20倍
* 作者定位有问题，以快为准，稳定性交给业务处理，但是又给出这么多模式跟业务绑在一起，再说是以他本身的工作经验为参考，不能解决所有问题，反而变得非常复杂，如果按照作者给出的模式来保证稳定性，性能不如其它的框架
