package main

import (
	log "github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"io/ioutil"
	"os"
	"testing"
)

func TestWhatever(t *testing.T) {
	// log2.SetFlags(flag int)

	// log2.LstdFlags
	// hm, what would happen if I do this?
	assert.Equal(t, os.IsNotExist(nil), false)
	assert.Equal(t, os.IsExist(nil), false)

}

func TestSimplifiedCleanUp(t *testing.T) {
	WithTempDir(t, func(temp_dir string) {
		most_top := jp(temp_dir, "hello/world/")
		dir := jp(most_top, "new/and/large/data/input/point/blab")
		dorothy := jp(dir, "dorothy.mp4")
		dir_list := []string{
			jp(most_top, "new/and/large/data/input/point/blab"),
			jp(most_top, "new/and/large/data/input/point/"),
			jp(most_top, "new/and/large/data/input/"),
			jp(most_top, "new/and/large/data/"),
			jp(most_top, "new/and/large/"),
			jp(most_top, "new/and/"),
			jp(most_top, "new/"),
			jp(most_top, ""),
		}
		// creating stuff
		assert.Equal(t, fs_backend.Create(dorothy), nil)
		assert.Equal(t, MustIsFSExist(dorothy), true)

		// now, should not remove, because of "dorothy.mp4"
		assert.Equal(t, SimplifiedCleanUp(temp_dir), nil)
		assert.Equal(t, MustIsFSExist(dir), true)

		// but, when we remove "dorothy.mp4" then clean up should also work
		log.SetLevel(log.DebugLevel)
		assert.Equal(t, os.Remove(dorothy), nil)
		assert.Equal(t, SimplifiedCleanUp(temp_dir), nil)
		assert.Equal(t, MustIsFSExist(dir), false)
		for _, v := range dir_list {
			assert.Equal(t, MustIsFSExist(v), false, "This should not exist: "+v)
		}
	})

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

func WithTempDir(t *testing.T, cb func(string)) {
	new_name, err := ioutil.TempDir("", "who_cares")
	assert.Equal(t, err, nil)
	cb(new_name)
	assert.Equal(t, os.RemoveAll(new_name), nil)
}

// it seems that path/filepath .Ext method doesn't work as I expected
func TestWithExt(t *testing.T) {
	zin := []string{"other", "meta", "tags", "of", "a", "file"}
	withExt("@super", []string{".hello"}, &zin, ".hello")
	assert.Equal(t, zin, []string{"other", "meta", "tags", "of", "a", "file", "@super"})
}
func TestFileMovingBug(t *testing.T) {
	WithTempDir(t, func(temp_dir string) {
		assert.Equal(t, fs_backend.Create(jp(temp_dir, "demo.mp4")), nil)
		movenew, err := DoFileMoving(jp(temp_dir, "demo.mp4"), jp(temp_dir, "zombie/zombie/blambie/dodo/demo.mp4"))
		assert.Equal(t, err, nil)
		assert.Equal(t, MustIsFileExist(jp(temp_dir, "demo.mp4")), false)
		assert.Equal(t, MustIsFileExist(movenew), true)
	})
}

func TestDoFileMoving(t *testing.T) {
	WithTempDir(t, func(temp_dir string) {
		assert.Equal(t, fs_backend.Create(jp(temp_dir, "maintain_this_dir.mp4")), nil)

		old_fpath := jp(temp_dir, "demo.mp4")
		nardie := []string{
			jp(temp_dir, ""),
			jp(temp_dir, "zombie/"),
			jp(temp_dir, "zombie/zombie/"),
			jp(temp_dir, "zombie/zombie/blambie/"),
			jp(temp_dir, "zombie/zombie/blambie/dodo/"),
		}
		new_fpath_dir := jp(temp_dir, nardie[4])
		new_fpath := jp(new_fpath_dir, "demo.mp4")
		new_fpath_after_collision_resolution := jp(new_fpath_dir, "demo_1.mp4")

		assert.Equal(t, fs_backend.Create(old_fpath), nil)

		dmf, err := DoFileMoving(old_fpath, new_fpath)
		assert.Equal(t, err, nil)
		assert.Equal(t, MustIsFileExist(old_fpath), false)
		assert.Equal(t, MustIsFileExist(new_fpath), true)
		assert.Equal(t, fs_backend.Create(old_fpath), nil)
		assert.Equal(t, dmf, new_fpath)

		dmf, err = DoFileMoving(old_fpath, new_fpath)
		assert.Equal(t, err, nil)
		assert.Equal(t, MustIsFileExist(old_fpath), false)
		assert.Equal(t, MustIsFileExist(new_fpath_after_collision_resolution), true)
		assert.Equal(t, dmf, new_fpath_after_collision_resolution)

		assert.Equal(t, os.Remove(new_fpath), nil)
		assert.Equal(t, os.Remove(new_fpath_after_collision_resolution), nil)

		SimplifiedCleanUp(temp_dir)
		for _, fpath := range nardie[1:] {
			assert.Equal(t, MustIsFSExist(fpath), false)
		}
		assert.Equal(t, MustIsFSExist(nardie[0]), true)
	})
}

func TestCleanUpEmptyDirectories(t *testing.T) {
	WithTempDir(t, func(temp_dir string) {
		f1 := []string{
			jp(temp_dir, ""),
			jp(temp_dir, "blab/"),
			jp(temp_dir, "blab/blip/"),
			jp(temp_dir, "blab/blip/blop/"),
			jp(temp_dir, "blab/blip/blop/tempo.mp4"),
		}
		f2 := []string{
			jp(temp_dir, ""),
			jp(temp_dir, "blab/"),
			jp(temp_dir, "blab/blip/"),
			jp(temp_dir, "blab/blip/tempo2.mp4"),
		}

		assert.Equal(t, fs_backend.Create(f1[4]), nil)
		assert.Equal(t, fs_backend.Create(f2[3]), nil)

		assert.Equal(t, SimplifiedCleanUp(temp_dir), nil)
		assert.Equal(t, MustIsFSExist(f1[3]), true)

		assert.Equal(t, os.Remove(f1[4]), nil)
		assert.Equal(t, SimplifiedCleanUp(temp_dir), nil)
		assert.Equal(t, MustIsFSExist(f1[3]), false)
		// because, we have another file: under the name tempo2.mp4, removing here should halt
		assert.Equal(t, MustIsFSExist(f1[2]), true)
		// remove now the file
		assert.Equal(t, os.Remove(f2[3]), nil)
		// do the clean up
		assert.Equal(t, SimplifiedCleanUp(temp_dir), nil)
		// so, no folders now should exist
		assert.Equal(t, MustIsFSExist(f2[2]), false)
		assert.Equal(t, MustIsFSExist(f2[1]), false)
	})
}
