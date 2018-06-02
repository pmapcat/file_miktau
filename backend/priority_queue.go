package main

import (
	"container/heap"
	"errors"
)

// priority queue, tailored for collector's usecase.
// Meaning, the following is implemented:
//    * .UserPop() => "blab"
//    * .UserAdd(item,rank) => nil
//    * .UserCount() => 23
// * .UserIntoList() => []string{}
// Due to some Go idiosynchrazies, other methods are existent, public and callable, but has no meaning here, and sometimes can cause panic when called
// thus, everything that is meant to be called here, is prefixed with `User`, and newFacetCollector

// also, all the code that is not prefixed with User, is directly copy pasted from here: https://golang.org/pkg/container/heap/#example__priorityQueue
// because, again, there is no generic mechanics in GO that can be used to not to do that.
// also, several new types are introduced: FacetCollectorItem and FacetCollector
// which is also code smell, and should be avoided, but, again there is no mechanics for this in Go

type FacetCollectorItem struct {
	value    string
	priority int
	index    int
}

// A PriorityQueue implements heap.Interface and holds Items.
type FacetCollector []*FacetCollectorItem

func (pq FacetCollector) Len() int { return len(pq) }

func (pq FacetCollector) Less(i, j int) bool {
	return pq[i].priority > pq[j].priority
}

func (pq FacetCollector) Swap(i, j int) {
	pq[i], pq[j] = pq[j], pq[i]
	pq[i].index = i
	pq[j].index = j
}

func (pq *FacetCollector) Push(x interface{}) {
	n := len(*pq)
	item := x.(*FacetCollectorItem)
	item.index = n
	*pq = append(*pq, item)
}

func (pq *FacetCollector) Pop() interface{} {
	old := *pq
	n := len(old)
	item := old[n-1]
	item.index = -1 // for safety
	*pq = old[0 : n-1]
	return item
}

func (pq *FacetCollector) update(item *FacetCollectorItem, value string, priority int) {
	item.value = value
	item.priority = priority
	heap.Fix(pq, item.index)
}

func newFacetCollector() FacetCollector {
	pq := FacetCollector{}
	heap.Init(&pq)
	return pq
}

func (pq FacetCollector) UserAdd(term string, val int) {
	heap.Push(&pq, FacetCollectorItem{value: term, priority: val})
}

// eiteher add, if collected lower than given limit
// or, remove smallest, and add new, if its priority is larger than given
func (pq FacetCollector) UserAddWithLimit(max int, new_term string, new_priority int) {
	// max bucket size is 0, nothing to do here
	if max == 0 {
		return
	}
	// bucket is full, removing extra items
	if pq.Len() >= max {
		old_term, old_priority, err := pq.UserPop()
		// empty priority queue, ergo max == 0, ergo cannot add anything with max amount 0
		if err != nil {
			return
		}
		// add new item
		if old_priority < new_priority {
			pq.UserAdd(new_term, new_priority)
			return
		}
		// otherwise, just return back what was popped
		pq.UserAdd(old_term, old_priority)
		return
	}
	// otherwise (not fully filled bucket), just add whatever comes in
	pq.UserAdd(new_term, new_priority)
}
func (pq FacetCollector) UserPop() (string, int, error) {
	if pq.Len() > 0 {
		item := heap.Pop(&pq).(*FacetCollectorItem)
		return item.value, item.priority, nil
	}
	return "", 0, errors.New("Empty Priority Queue")
}

func (pq FacetCollector) UserToList() []string {
	result := []string{}
	for pq.Len() > 0 {
		item := heap.Pop(&pq).(*FacetCollectorItem)
		result = append(result, item.value)
	}
	return result
}
