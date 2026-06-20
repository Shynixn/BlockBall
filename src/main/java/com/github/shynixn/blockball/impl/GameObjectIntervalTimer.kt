package com.github.shynixn.blockball.impl

class GameObjectIntervalTimer(private var intervalMs: Int) {
    private var timer: Int = 0

    fun update(deltaMs: Int): Boolean {
        timer += deltaMs;
        if (timer >= intervalMs) {
            timer = 0
            return true;
        }
        return false;
    }
}