package main

import (
	"errors"
	"github.com/boltdb/bolt"
	"path/filepath"
	"strings"
)

// ### How would it look like (work)?
// On init (read):
// * Read first source of truth (File System)
// * Then, read "patch" from a database file (in the same directory):
//   * In patch ("what directories/tags are "removed")
//   * What directories "tags" are added.
//   * Apply patch

// On update (write):
//   * Patch data resolution:
//     * Write down what to delete (into delete, of patch)
//     * Remove, (what to delete) from (what to add) on patch
//     * Remove (what to add) from (what to delete) on patch
//     * Save patched data into a patch database.

type patch_db_ struct{}

var patch_db = patch_db_{}

func (p *patch_db_) retrievePatchData(root string) (map[string]*PatchRecord, error) {
	result := map[string]*PatchRecord{}

	patchdb := filepath.Join(root, PATCH_DB_PREFIX)
	if !IsFileExist(patchdb) {
		return result, errors.New("There is no patch db for this usecase: " + patchdb)
	}
	db, err := bolt.Open(patchdb, 0777, nil)
	if err != nil {
		return result, err
	}
	defer db.Close()
	return result, db.View(func(tx *bolt.Tx) error {
		bucket := tx.Bucket(PATCH_DB_BUCKET)
		if bucket == nil {
			return errors.New("No bucket")
		}
		bucket.ForEach(func(k []byte, v []byte) error {
			pr := PatchRecord{}
			if err := LogErr("Cannot unmarshal data", pr.UnmarshalBinary(v)); err != nil {
				return err
			}
			result[string(k)] = &pr
			return nil
		})
	})
}

func (p *patch_db_) MakeEditPatch(filepath string, old_tags []string,new_tags []string) *PatchRecord {
	
}

func (p *patch_db_) ApplyEditDistance(root string, []*PatchRecord) error {

}

// will apply user patch to a data set
func (p *patch_db_) OnLoadApplyPatch(root string, items []*CoreNodeItem) ([]*CoreNodeItem, error) {
	patchdb, err := p.retrievePatchData(root)
	if err != nil {
		return items, err
	}

	for k, v := range items {
		patch, ok := patchdb[strings.TrimPrefix(v.FilePath, root)]
		if !ok {
			continue
		}
		v.AddTags(patch.TagsAdded)
		v.RemoveTags(patch.TagsRemoved)
		items[k] = v
	}

}
