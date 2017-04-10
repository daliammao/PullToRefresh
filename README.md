#PullToRefresh
<br/>
下拉属性与上拉加载的解决方案，继承于ViewGroup可以包含任何View。功能比SwipeRefreshLayout强大。

[APK下载](https://github.com/daliammao/PullToRefresh/raw/master/app/apk/app-debug.apk)

* 阻尼系数

    默认: `1.7f`，越大，感觉下拉时越吃力。

* 触发刷新时移动的位置比例

    默认，`1.2f`，移动达到头部高度1.2倍时可触发刷新操作。

* 回弹延时

    默认 `200ms`，回弹到刷新高度所用时间

* 头部回弹时间

    默认`1000ms`

* 刷新是保持头部

    默认值 `true`.

* 下拉刷新 / 释放刷新

    默认为释放刷新

# 处理刷新


通过`PtrHandler`，可以检查确定是否可以下来刷新以及在合适的时间刷新数据。

检查是否可以下拉刷新在`PtrDefaultHandler.checkContentCanBePulledDown`中有默认简单的实现，
检查是否可以上拉加载在`PtrDefaultHandler.checkContentCanBePulledUp`中有默认简单的实现，
你可以根据实际情况完成这个逻辑。


```
public interface PtrHandler {

    /**
     * 检查是否可以执行下拉刷新，比如列表为空或者列表第一项在最上面时。
     * <p/>
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * 检查是否可以执行上拉加载。
     * <p/>
     */
    public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View footer) {
    }

    /**
     * 需要刷新数据时触发
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);
}
    /**
     * 需要加载数据时触发
     *
     * @param frame
     */
    public void onLoadBegin(PtrFrameLayout frame){
    }
```
例子:

```java
    mPtrFrame.setPtrHandler(new PtrHandler() {
        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
                     updateData();
       }

        @Override
        public void onLoadBegin(PtrFrameLayout frame) {
            LoadData();
        }

        @Override
        public boolean checkCanDoDownRefresh(PtrFrameLayout frame, View content, View header) {
            return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
        }

        @Override
        public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View footer) {
            return PtrDefaultHandler.checkContentCanBePulledUp(frame, content, footer);
        }
    });
```

<br/>
感谢liaohuqiu 提供的 [android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh) 本项目基于该项目开发，不同的是我添加了上拉加载的功能。