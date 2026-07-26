package com.gtnewhorizons.stargatenh.common.util;

import java.util.Arrays;

public final class StargateAddress {

    public final int[] sigils;

    public StargateAddress(int[] sigils) {
        this.sigils = Arrays.copyOf(sigils, 7);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StargateAddress other)) return false;
        return Arrays.equals(this.sigils, other.sigils);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(sigils);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            builder.append(getSigilChar(sigils[i]));
            builder.append(' ');
        }
        return builder.toString();
    }

    public char getSigilChar(int i) {
        return switch (i) {
            case 0 -> 'A';
            case 1 -> 'O';
            case 2 -> 'T';
            case 3 -> 'D';
            case 4 -> 'P';
            case 5 -> 'F';
            case 6 -> 'H';
            case 7 -> 'L';
            case 8 -> 'M';
            case 9 -> 'I';
            case 10 -> 'E';
            case 11 -> 'J';
            case 12 -> 'G';
            case 13 -> 'K';
            case 14 -> 'B';
            case 15 -> 'C';
            default -> 'Z';
        };
    }
}
