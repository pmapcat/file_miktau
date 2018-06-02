package main

import (
	"sort"
)

type MetaThesaurusAndSortingAggregator struct {
	thesaurus map[string]int
	context   map[string]map[string]int
}

func (t *MetaThesaurusAndSortingAggregator) OnBeforeRun() {
	t.reinit()
}
func (t *MetaThesaurusAndSortingAggregator) reinit() {
	t.thesaurus = map[string]int{}
	t.context = map[string]map[string]int{}
}

func (t *MetaThesaurusAndSortingAggregator) GetThesaurus() map[string]int {
	return t.thesaurus
}
func (T *MetaThesaurusAndSortingAggregator) GetTagContext() map[string]map[string]int {
	return T.context
}

func (t *MetaThesaurusAndSortingAggregator) Accumulate(node *CoreNodeItem) {
	for _, tag := range node.MetaTags {
		// thesaurus accumulation
		t.thesaurus[tag] += 1
		// context accumulation
		item, ok := t.context[tag]
		if !ok {
			item = map[string]int{}
		}
		if len(item) < TAG_CONTEXT_MAX_SIZE {
			for _, btag := range node.Tags {
				if btag == tag {
					continue
				}

				if len(item) > TAG_CONTEXT_MAX_SIZE {
					break
				}
				item[btag] += 1
			}
			t.context[tag] = item
		}
	}
}

func (t *MetaThesaurusAndSortingAggregator) Aggregate(m *CoreNodeItem) {
	sort.Slice(m.MetaTags, func(i int, j int) bool {
		return t.thesaurus[m.MetaTags[i]] > t.thesaurus[m.MetaTags[j]]
	})
}
