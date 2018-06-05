package main

type mergers_ struct{}

var mergers = mergers_{}

func (m *mergers_) MergeTagContexts(a map[string]map[string]int, b map[string]map[string]int) map[string]map[string]int {
	result := map[string]map[string]int{}
	for k, v := range a {
		result[k] = v
	}
	for k, v := range b {
		result[k] = v
	}
	return result

}
func (m *mergers_) MergeThesaurus(a map[string]int, b map[string]int) map[string]int {
	result := map[string]int{}
	for k, v := range a {
		result[k] = v
	}
	for k, v := range b {
		result[k] = v
	}
	return result

}
