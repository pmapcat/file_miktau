// Copyright 2009 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

// This file contains several overwritten functions on
// path/filepath of Golang. There are several reasons
// for this:
//  * I need depth first traversal of a directory tree
//    (thus, reimplementation of Walk)
//  * I don't care about order of directories on a
//    given level, (reimplementation of read_dir_names)

package main

import (
	"os"
	"path/filepath"
)

type path_ struct{}

var SkipDir = filepath.SkipDir

var path = path_{}

func (p *path_) readDirNames(dirname string) ([]string, error) {
	f, err := os.Open(dirname)
	if err != nil {
		return nil, err
	}
	names, err := f.Readdirnames(-1)
	f.Close()
	if err != nil {
		return nil, err
	}
	return names, nil
}

// walk recursively descends path, calling w.
func (p *path_) walk(path string, info os.FileInfo, walkFn func(path string, info os.FileInfo, err error) error) error {

	if !info.IsDir() {
		return nil
	}

	names, err := p.readDirNames(path)
	if err != nil {
		return walkFn(path, info, err)
	}
	for _, name := range names {
		filename := filepath.Join(path, name)
		fileInfo, err := os.Lstat(filename)
		if err != nil {
			if err := walkFn(filename, fileInfo, err); err != nil && err != filepath.SkipDir {
				return err
			}
		} else {
			err = p.walk(filename, fileInfo, walkFn)
			if err != nil {
				if !fileInfo.IsDir() || err != filepath.SkipDir {
					return err
				}
			}
		}
	}

	err = walkFn(path, info, nil)
	if err != nil {
		if info.IsDir() && err == SkipDir {
			return nil
		}
		return err
	}

	return nil
}

// Walk walks the file tree rooted at root, calling walkFn for each file or
// directory in the tree, including root. All errors that arise visiting files
// and directories are filtered by walkFn. The files are walked in lexical
// order, which makes the output deterministic but means that for very
// large directories Walk can be inefficient.
// Walk does not follow symbolic links.
func (p *path_) Walk(root string, walkFn func(path string, info os.FileInfo, err error) error) error {
	info, err := os.Lstat(root)
	if err != nil {
		err = walkFn(root, nil, err)
	} else {
		err = p.walk(root, info, walkFn)
	}
	if err == filepath.SkipDir {
		return nil
	}
	return err
}
