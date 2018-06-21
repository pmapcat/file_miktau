package main

func AppStateItemIdentity(a *AppStateItem) *AppStateItem {
	return a
}

func MultifySingleHook(cb func(*AppStateItem)) func([]*AppStateItem) {
	return func(items []*AppStateItem) {
		for _, v := range items {
			cb(v)
		}
	}
}
