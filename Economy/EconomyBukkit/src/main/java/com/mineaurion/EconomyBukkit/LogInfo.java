package com.mineaurion.EconomyBukkit;

import java.io.Serializable;

public enum LogInfo implements Serializable {
	SET,GIVE,TAKE, RESET, PAY, CHECK;

	private LogInfo() {
	}
}
