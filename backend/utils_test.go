package main

import (
	"github.com/stretchr/testify/assert"
	"math/rand"
	"os"
	"testing"
)

// it seems that path/filepath .Ext method doesn't work as I expected
func TestWithExt(t *testing.T) {
	zin := []string{"other", "meta", "tags", "of", "a", "file"}
	withExt("@super", []string{".hello"}, &zin, ".hello")
	assert.Equal(t, zin, []string{"other", "meta", "tags", "of", "a", "file", "@super"})
}

func TestDoFileMoving(t *testing.T) {
	assert.Equal(t, os.Create("test_data/demo.mp4"), nil)
	assert.Equal(t, DoFileMoving("test_data/demo.mp4", "test_data/zombie/zombie/blambie/dodo/demo.mp4"), nil)
	assert.Equal(t, IsFileExist("test_data/demo.mp4"), false)
	assert.Equal(t, IsFileExist("test_data/zombie/zombie/blambie/dodo/demo.mp4"), true)

	assert.Equal(t, os.Create("test_data/demo2.mp4"), nil)
	assert.Equal(t, DoFileMoving("test_data/demo2.mp4", "test_data/zombie/zombie/blambie/dodo/demo.mp4"), nil)
	assert.Equal(t, IsFileExist("test_data/demo2.mp4"), false)
	assert.Equal(t, IsFileExist("test_data/zombie/zombie/blambie/dodo/demo_1.mp4"), true)

	assert.Equal(t, os.Remove("test_data/zombie/zombie/blambie/dodo/demo.mp4"), nil)
	assert.Equal(t, os.Remove("test_data/zombie/zombie/blambie/dodo/demo_1.mp4"), nil)
	assert.Equal(t, CleanUpEmptyDirectories("test_data/", "test_data/zombie/zombie/blambie/dodo/"), nil)
	assert.Equal(t, IsFileExist("test_data/zombie/"), false)
}
func TestCleanUpEmptyDirectories(t *testing.T) {
	assert.Equal(t, os.MkdirAll("test_data/blab/blip/blop", 0777), nil)
	assert.Equal(t, os.Create("test_data/blab/blip/blop/tempo.mp4"), nil)
	assert.Equal(t, os.Create("test_data/blab/blip/tempo2.mp4"), nil)

	assert.Equal(t, CleanUpEmptyDirectories("test_data/", "test_data/blab/blip/blop/tempo.mp4").Error(), "Not a directory: test_data/blab/blip/blop/tempo.mp4")
	assert.Equal(t, CleanUpEmptyDirectories("test_data/", "test_data/blab/blip/blop/").Error(), "not empty")
	assert.Equal(t, os.Remove("test_data/blab/blip/blop/tempo.mp4"), nil)

	assert.Equal(t, IsFileExist("test_data/blab/blip/blop/"), true)
	assert.Equal(t, CleanUpEmptyDirectories("test_data/", "test_data/blab/blip/blop/"), "not-empty")
	assert.Equal(t, IsFileExist("test_data/blab/blip/blop/"), false)
	assert.Equal(t, IsFileExist("test_data/blab/blip/"), true)
	assert.Equal(t, os.Remove("test_data/blab/blip/tempo2.mp4"), nil)
	assert.Equal(t, CleanUpEmptyDirectories("test_data/", "test_data/blab/blip/"), nil)
	assert.Equal(t, IsFileExist("test_data/blab/"), false)
	assert.Equal(t, IsFileExist("test_data/"), true)

}
func TestGeneratingCollisionFreeFileName(t *testing.T) {
	wdir := "tempo/"
	create := func(fpath string) {
		_, err := os.Create(fpath)
		assert.Equal(t, err, nil)
	}
	assert.Equal(t, WithDir(wdir, func() {
		assert.Equal(t, GenerateCollisionFreeFileName(wdir, ""), "undefined")
		assert.Equal(t, GenerateCollisionFreeFileName(wdir, "hello.mp4"), "hello.mp4")
		create(wdir + "hello.mp4")
		assert.Equal(t, GenerateCollisionFreeFileName(wdir, "hello.mp4"), "hello_1.mp4")
		create(wdir + "hello_1.mp4")
		assert.Equal(t, GenerateCollisionFreeFileName(wdir, "hello.mp4"), "hello_2.mp4")
		create(wdir + "hello_2.mp4")
		assert.Equal(t, GenerateCollisionFreeFileName(wdir, "hello.mp4"), "hello_3.mp4")
		// e.t.c.
	}), nil)
}

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
