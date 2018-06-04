package main

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	"math/rand"
	"strconv"
	"testing"
)

func TestPriorityQueue(t *testing.T) {
	fc := newFacetCollector()
	fc.UserAdd("hello", 1)
	fc.UserAdd("world", 2)
	fc.UserAdd("is", 0)
	fc.UserAdd("sure", 100)
	fc.UserAdd("thing", 50)
	assert.Equal(t, fc.UserToList(), []string{"sure", "thing", "world", "hello", "is"})

	perfsize := 100000
	// expect larger -> largest amount on return
	timeEval(fmt.Sprintf("Collect facet on this amount %v", perfsize), func() {
		for i := 0; i <= perfsize; i++ {
			ao := rand.Intn(1000)
			fc.UserAddWithLimit(10, strconv.Itoa(ao), ao)
		}
	})
	fcu := fc.UserToList()
	assert.Equal(t, len(fcu), 10)
	assert.True(t, forTestingStringToInt(fcu[0]) >= forTestingStringToInt(fcu[1]))
	assert.True(t, forTestingStringToInt(fcu[8]) >= forTestingStringToInt(fcu[9]))
}
