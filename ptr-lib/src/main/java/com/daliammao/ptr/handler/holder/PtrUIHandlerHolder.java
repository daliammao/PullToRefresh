package com.daliammao.ptr.handler.holder;

import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrUIHandler;
import com.daliammao.ptr.indicator.PtrIndicator;

/**
 * @author: zhoupengwei
 * @time:15/12/2-下午5:14
 * @Email: 496946423@qq.com
 * @desc: 一个简单的列表
 */
public class PtrUIHandlerHolder implements PtrUIHandler {

    protected PtrUIHandler mHandler;
    protected PtrUIHandlerHolder mNext;

    protected boolean contains(PtrUIHandler handler) {
        return mHandler != null && mHandler == handler;
    }

    public boolean hasHandler() {
        return mHandler != null;
    }

    protected PtrUIHandler getHandler() {
        return mHandler;
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    public static void addHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {

        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }

        PtrUIHandlerHolder current = head;
        for (; ; current = current.mNext) {

            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        PtrUIHandlerHolder current = head;
        PtrUIHandlerHolder pre = null;
        do {

            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {

                // current is head
                if (pre == null) {

                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {

                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }

        } while (current != null);

        if (head == null) {
            head = new PtrUIHandlerHolder();
        }
        return head;
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPrepare(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIBegin(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIComplete(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}

