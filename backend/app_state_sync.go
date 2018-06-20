package main

import (
	"sync"
)

type AppStateSync struct {
	sync.RWMutex
	aps *AppState
}

func WrapSync(a *AppState) *AppStateSync {
	return &AppStateSync{aps: a}
}
func (n *AppStateSync) AppState() *AppState {
	return n.aps
}

func (n *AppStateSync) SwapAppState(a *AppState) {
	n.aps = a
}
