package main

import (
	"github.com/stretchr/testify/assert"
	"math/rand"
	"testing"
)

func TestIsSubset(t *testing.T) {
	assert.Equal(t, is_subset([]string{"a", "b", "c"}, []string{"a", "b"}), true)
	assert.Equal(t, is_subset([]string{"a", "c"}, []string{"a", "b"}), false)
	assert.Equal(t, is_subset([]string{"a", "c"}, []string{"a", "c"}), true)
	assert.Equal(t, is_subset([]string{"a", "c"}, []string{"a"}), true)
	assert.Equal(t, is_subset([]string{"a", "c"}, []string{}), true)
	assert.Equal(t, is_subset([]string{}, []string{}), true)
	assert.Equal(t, is_subset([]string{}, []string{"a"}), false)
}

func TestPaginatorToSlice(t *testing.T) {
	node_items := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}
	left, right, last_page := PaginatorToSlice(len(node_items), 3, 1)
	assert.Equal(t, last_page, 5)
	assert.Equal(t, node_items[left:right], []int{1, 2, 3})

	_, _, last_page = PaginatorToSlice(len(node_items), 3, 1)
	left, right, last_page = PaginatorToSlice(len(node_items), 3, last_page)
	assert.Equal(t, node_items[left:right], []int{13, 14, 15})

	_, _, last_page = PaginatorToSlice(len(node_items), 1, 1)
	left, right, last_page = PaginatorToSlice(len(node_items), 1, last_page)
	assert.Equal(t, node_items[left:right], []int{15})

	_, _, last_page = PaginatorToSlice(len(node_items), 30, 1)
	assert.Equal(t, last_page, 1)
	left, right, last_page = PaginatorToSlice(len(node_items), 30, last_page)
	assert.Equal(t, node_items[left:right], []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})

	_, _, last_page = PaginatorToSlice(len(node_items), 0, 1)
	left, right, last_page = PaginatorToSlice(len(node_items), 0, last_page)
	assert.Equal(t, left, 0)
	assert.Equal(t, right, 10)

	assert.Equal(t, node_items[left:right], node_items[:10])

	node_items = []int{}
	left, right, last_page = PaginatorToSlice(len(node_items), 3, 1)
	assert.Equal(t, node_items[left:right], []int{})
	// stress test. This shouldn't fail
	node_items = []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}
	for i := 0; i <= 10000; i++ {
		a, b, _ := PaginatorToSlice(len(node_items), rand.Int(), rand.Int())
		assert.NotPanics(t, func() {
			func() []int {
				return node_items[a:b]
			}()
		})
	}

}
