(ns miktau.api-handler.query-building-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.api-handler.query-builder :as query-building]
            [miktau.test-utils :refer [with-diff]]))


(deftest test-build-core-query []
  (with-diff (query-building/build-core-query nil nil nil nil "not-working")
    {:modified {}, :sorted "", :tags [] :file-paths []})
  
  (is (=  (query-building/build-core-query "" #{"*"} #{:nos :gos :dos} {:year 2018, :day 23, :month 11} nil)
          {:modified {:year 2018, :day 23, :month 11},
           :sorted ""
           :file-paths []
           :tags ["dos" "gos" "nos"]}))
  
    (is (=  (query-building/build-core-query "-name" #{"*"} #{:blab} {:year 2018, :day 23, :month 11} nil)
          {:modified {:year 2018, :day 23, :month 11}
           :sorted   "-name"
           :file-paths []
           :tags ["blab"]}))
  
  (is (= (query-building/build-core-query nil  #{"a" "b"} nil nil "not-working")
         {:modified {}, :sorted "", :file-paths ["a" "b"], :tags []}))
  
  (is (= (query-building/build-core-query "" #{"*" "/blab" "/blip" "blob"} #{:blab} {:year 2018, :day 23, :month 11} nil)
         {:modified {:year 2018, :day 23, :month 11} :sorted   "" :file-paths [] :tags ["blab"]}))
  
  (is (=  (query-building/build-core-query "" #{} #{:blab} {:year 2018, :day 23, :month 11} "not-working")
          {:modified {:year 2018, :day 23, :month 11} :sorted "" :tags ["blab"] :file-paths []}))
  
  (is (=  (query-building/build-core-query "" #{"/blab" "/blip" "blob"} #{:blab} {:year 2018, :day 23, :month 11}  nil)
          {:file-paths ["/blab" "/blip" "blob"]  :tags [] :modified {}, :sorted ""})))

(deftest test-build-bulk-operate-on-files []
  (is (= (query-building/build-bulk-operate-on-files :zerio  #{"/blab" "/blip" "blob"} #{:blab} {:year 2018, :day 23, :month 11}  "not-working") "not-working"))
  ;; (is (= (query-building/build-bulk-operate-on-files :symlinks #{"/blab" "/blip" "blob"} #{:blab} {:year 2018, :day 23, :month 11}  "not-working")
  ;;        {:url "/api/bulk-operate-on-files"
  ;;         :params {:action "symlinks"
  ;;                  :request  {:file-paths ["/blab" "/blip" "blob"]}}}))
  (is (= (query-building/build-bulk-operate-on-files :symlinks #{"/blab" "/blip" "blob"} #{:blab} {:year 2018, :day 23, :month 11}  "not-working")
          {:url "/api/bulk-operate-on-files", :params {:action "symlinks", :request {:modified {}, :sorted "", :file-paths ["/blab" "/blip" "blob"], :tags []}}})))


(deftest test-build-update-records []
  (is (= (query-building/build-update-records nil nil nil nil nil "not-working") "not-working"))
  (with-diff
    (query-building/build-update-records "a b c" #{:ho :ro :so} #{"*"}  #{:blab} {:year 2018, :day 23, :month 11}  "not-working")
    {:url "/api/update-records"
     :params {:tags-to-add ["a" "b" "c"]
              :tags-to-delete ["ho" "ro" "so"]
              :request  {:modified {:year 2018, :day 23, :month 11}, :sorted "",
                         :file-paths []
                         :tags ["blab"]}}})
  (is (= (query-building/build-update-records nil nil #{"*"}  #{:blab} {:year 2018, :day 23, :month 11}  "not-working")
         "not-working")))


