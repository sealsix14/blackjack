package com.ood.blackjack.Rules;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class Turn<T> implements Serializable {

        public interface OnTurnStartedListener<T> extends Serializable {
                void onTurnStarted(T currentPlayer);
        }

        public interface OnTurnEndedListener<T> extends Serializable {
                void onTurnEnded(T currentPlayer);
        }
        
        public interface OnRoundEndedListener extends Serializable {
                void onRoundEnded();
        }

        private static final long serialVersionUID = 1L;
        private static final String LOG_TAG = "TURN";
        private ArrayList<T> players;
        private int turnIndex;
        private int roundIdx;
        private transient ArrayList<OnTurnStartedListener<T>> turnStartListenerList;
        private transient ArrayList<OnTurnEndedListener<T>> turnEndListenerList;
        private transient ArrayList<OnRoundEndedListener> roundEndedListenerList;

        public Turn() {
                throw new UnsupportedOperationException("cannot init without players");
        }

        public Turn(ArrayList<T> players, int startingPlayerIndex) {
                this.players = players;
                newRound(startingPlayerIndex, true);
        }

        public void newRound(int startingPlayerIndex, boolean isFirstRound) {
                this.turnIndex = startingPlayerIndex;
                
                if (!isFirstRound){
                        this.roundIdx++;
                }
                else {
                        this.roundIdx = 0;
                }
        }

        public int getCurrentRoundNumber() {
                return roundIdx;
        }

        public T next() {
                
                Log.v(LOG_TAG, "next player");
                turnIndex = (turnIndex + 1) % players.size();
                if (turnIndex == 0) {
                        roundIdx++;
                        fireRoundEndedEvent(); 
                }

                T retVal = players.get(turnIndex);
                fireTurnStartEvent(retVal);
                fireTurnEndEvent(retVal);
                return retVal;
        }

        public void fireRoundEndedEvent() {
                if (roundEndedListenerList != null) {
                        for (OnRoundEndedListener l : roundEndedListenerList) {
                                l.onRoundEnded();
                        }
                }
        }

        public void fireTurnStartEvent(T startingEntity) {
                if (turnStartListenerList != null) {
                        for (OnTurnStartedListener<T> l : turnStartListenerList) {
                                l.onTurnStarted(startingEntity);
                        }
                }
        }
        public void fireTurnEndEvent(T startingEntity) {
                if (turnEndListenerList != null) {
                        for (OnTurnEndedListener<T> l : turnEndListenerList) {
                                l.onTurnEnded(startingEntity);
                        }
                }
        }

        public T peek() {
                return players.get(turnIndex);
        }

        public T peekNext() {
                return players.get((turnIndex + 1) % players.size());
        }
        public void clearOnTurnStartedListenerList(){
                this.turnStartListenerList = new ArrayList<OnTurnStartedListener<T>>();
        }
        public void addOnTurnStartedListener(OnTurnStartedListener<T> l) {
                if (turnStartListenerList == null) {
                        // Since turnEndListenerList has to be transient,
                        // we lazy load it to make sure that it exists even
                        // if the turn class deserialized and it was not deserialized
                        turnStartListenerList = new ArrayList<OnTurnStartedListener<T>>();
                }
                this.turnStartListenerList.add(l);
        }

        public void addOnRoundEndedListener(OnRoundEndedListener l) {
                if (roundEndedListenerList == null) {
                        // Since roundEndListenerList has to be transient,
                        // we lazy load it to make sure that it exists even
                        // if the turn class deserialized and it was not deserialized
                        roundEndedListenerList = new ArrayList<OnRoundEndedListener>();
                }
                this.roundEndedListenerList.add(l);
        }
}