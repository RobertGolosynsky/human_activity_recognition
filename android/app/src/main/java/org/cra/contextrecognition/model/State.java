package org.cra.contextrecognition.model;

public enum  State {
    WALK, STAND, SIT;

    State() {
    }

    public static State fromLiteral(int i){
        State[] states = State.values();
        return states[i%states.length];

    }
}
