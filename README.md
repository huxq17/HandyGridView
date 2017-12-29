# HandyGridView

[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-HandyGridView-green.svg?style=flat )]( https://android-arsenal.com/details/1/6571 )

HandyGridView本质上是一个GridView，所以你也可以当成普通的GridView来使用，HandyGridView继承了GridView并在此之上添加了item拖动和交换，绘制图文等功能。
由于只是一个GridView，所以性能比目前其他大部分解决方案都要好。

HandyGridView is a high-performance drag and drop GridView, it extends GridView, you can drag drop GridView item to sort the labels,and draw something on the GridView. Just use the HandyGridView like a GridView.

---

### Screenshots

 <img src="art/art.gif" width="280" height="475" />

---
### Usage

#### Gradle

```groovy
dependencies {
   compile 'com.huxq17.handygridview:handygridview:1.1.0'

}
```

#### minSdkVersion 11

#### HandyGridView's three modes：

Mode | introduction
---|---
TOUCH | Edit mode，the item can be dragged
LONG_PRESS | Long press mode，item can be dragged after long press.
NONE | Item can not be dragged, jsut like normal GridView.

Usage：

```
HandyGridView#setMode(TOUCH|LONG_PRESS|NONE);

```

#### Adapter

HandyGridView会在item被拖动交换时发出通知，如果想要做出对应数据上的变化，则可以在Apdater中实现OnItemMovedListener，示例如下：

HandyGridView will send a notification to notify you swip the data source when its item's order is changed. the usage is as follows:

```

public class GridViewAdapter extends BaseAdapter implements OnItemMovedListener｛
    @Override
    public void onItemMoved(int from, int to) {
        String s = mDatas.remove(from);
        mDatas.add(to, s);
    }

    @Override
    public boolean isFixed(int position) {
        //When postion==0,the item can not be dragged.
        if (position == 0) {
            return true;
        }
        return false;
    }
｝
```

#### 绘制图文
HandyGridView提供了在gridview上绘制图文的接口，示例如下：

You can draw something on HandyGridView, the usage is as follows:

```
    mGridView.setDrawer(new IDrawer() {
            @Override
            public void onDraw(Canvas canvas, int width, int height) {
                if (!mGridView.isNoneMode()) {
                    int offsetX = -DensityUtil.dip2px(MainActivity.this, 10);
                    int offsetY = -DensityUtil.dip2px(MainActivity.this, 10);
                    //文字绘制于gridview的右下角，并向左，向上偏移10dp。
                    //Draw text on the right-bottom of GridView.
                    drawTips(canvas, width + offsetX, height + offsetY);
                }
            }
        },false);

    private void drawTips(Canvas canvas, int width, int height) {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setColor(Color.parseColor("#CFCFCF"));
            mTextPaint.setTextSize(DensityUtil.dip2px(MainActivity.this, 12));
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            textHeight = (int) (fontMetrics.bottom - fontMetrics.top) + 1;
            textWidth = (int) mTextPaint.measureText(paintText) + 1;
        }
        width = width - textWidth;
        height = height - textHeight;
        if (tipsLayout == null) {
            tipsLayout = new StaticLayout(paintText, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0f, false);
        }
        canvas.translate(width, height);
        tipsLayout.draw(canvas);
    }

```


以上就是主要的用法了,[更多的用法可以参考example](https://github.com/huxq17/HandyGridView/blob/master/app/src/main/java/com/handygridview/example/MainActivity.java).

The above is the main usage,[click to get more](https://github.com/huxq17/HandyGridView/blob/master/app/src/main/java/com/handygridview/example/MainActivity.java).



---

### 更新日志<br/>

    2017-12-29：
    1.解决某些小米手机上item拖动交换时会闪烁的问题,更新到1.1.0。

---
### LICENSE

[Apache License 2.0](LICENSE)




