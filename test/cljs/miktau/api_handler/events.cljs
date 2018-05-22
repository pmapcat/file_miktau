

(comment
  (is (=   (utils/clean-server-call-for-tests (:http-xhrio (miktau-events/file-operation-fx {:db db} [nil :default])))
             {:method :post, :uri "/api/bulk-operate-on-files",
              :timeout 8000,
              :params {:action "default",
                       :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}},
              :on-success [:mutable-server-operation],
              :on-failure [:http-error]}))
  (is (= (utils/clean-server-call-for-tests (:http-xhrio (miktau-events/submit-tagging {:db db} nil)))
         {:method :post,
          :uri "/api/update-records",
          :timeout 8000,
          :params {:tags-to-add ["blop" "glop"], :tags-to-delete ["hom"], :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["hello" "hom"]}},
          :on-success [:edit-nodes/redirect-to-nodes],
          :on-failure [:http-error]})))
