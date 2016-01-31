package com.mirza.androidemojifix;

import com.txusballesteros.bubbles.BubbleLayout;

/**
 * Created by CR7 on 1/31/2016.
 */
public class RemoveBubbleEvent {
    private BubbleLayout bubbleLayout;

    public RemoveBubbleEvent(BubbleLayout bubbleLayout) {
        this.bubbleLayout = bubbleLayout;
    }

    public BubbleLayout getBubbleLayout() {
        return bubbleLayout;
    }
}
