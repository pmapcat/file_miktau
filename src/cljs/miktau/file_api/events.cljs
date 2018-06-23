(ns miktau.file-api.events)

;; send them to the server, redirect result to nodes, make them selected
;; what problems arise?
;; * No chosen root directory
;; * Files are not members of a given root directory
;; * Various I/O problems, namely:
;;   * root is not writable
;;   * root does not exist (no disk)
;;   * root is full
;;   * size of given file exceeds root size
;;   * directory structure is important to user
;;   * moving operation is costly
;;   *

;; What if?
;;  * turn folder structure into meta
;;  * write tags into a separate (at root) file.
;;    * on directory change will have to work with this
;;    * git? (on update operation reread the whole directory

(defn got-files
  [db [_ file-list]])

(defn chroot
  [db [_ new-root]]
  )





