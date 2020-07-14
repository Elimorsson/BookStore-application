package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;

public class TickBroadcast implements Broadcast {
    private int currentTick;

    public TickBroadcast(int tick) {
        currentTick = tick;
    }

    public int getCurrentTick() {
        return currentTick;
    }
}
