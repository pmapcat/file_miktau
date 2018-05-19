(ns miktau.edit-nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]))

(defn e->content
  [e]
  (str
   (aget e "target" "value" )))
(defn remove-tags-from-selection [nodes-changing]
  [:div
   [:h2.header-font.light-gray.mik-cut-bottom {:style {:font-size "1em"}}
    [views-utils/icon "local_offer"]
    "Remove tags from selection"]
   [:div 
    (for [tag (:tags-to-delete nodes-changing)]
      [:span.unstyled-link.padded-as-button
       {:key (:name tag)
        :class (if (:selected? tag) " crossed-out " "")
        :style {:cursor "pointer"  :display "inline-block"}
        :on-click #(refe/dispatch [:edit-nodes/delete-tag-from-selection (:key-name tag)])} 
       (:name tag) " "])]])

(defn group-operations [nodes-changing]
  [:div.padded-as-button
   [:h2.mik-cut-bottom.gray "Open selected files"]
   (if (>  (:total-amount nodes-changing) 20)
     [:span {:key "if-more-than-N"}
      [:p.warning
       [:i.material-icons {:style {:font-size "1.2em" :float "right"}} "warning"]
       "You've selected " [:b ] (:total-amount nodes-changing) " files" [:br]
       "Opening them all will hang your computer" [:br]
       "Reduce amount of files to less than 20 in a selection" [:br]
       "To successfuly open them"]]
     [:span {:key "if-less-than-N"}
      [:a.mik-cut-left.unstyled-link.pure-button {:key "in a single folder" :href "#" :on-click #(refe/dispatch [:edit-nodes/file-operation :in-folder])}
       [views-utils/icon "folder_open"] " In a single folder"]
      [:a.unstyled-link.pure-button  {:key "each individually" :href "#" :on-click #(refe/dispatch [:edit-nodes/file-operation :individually])}
       [views-utils/icon "list"] " Each individually"]
      [:a.unstyled-link.pure-button  {:key "individually" :href "#"  :on-click #(refe/dispatch [:edit-nodes/file-operation :default-program])}
       [views-utils/icon "filter"] " Each in default program"]])])
(defn add-tags-to-selection [nodes-changing]
  [:div
   [:h2.header-font.light-gray.mik-cut-bottom {:style {:font-size "1em"}}
    [views-utils/icon "local_offer"]
    "Add tags to selection"]
   [:div
    [:textarea.padded-as-button
     {:placeholder "tag_one, tag_another, tag_third, tag_nth"
      :style  {:width "98%" :height "100px" :resize "none" }
      :on-change #(refe/dispatch [:edit-nodes/add-tags-to-selection (e->content %)])
      :value  (:tags-to-add nodes-changing)}]]])

(defn main []
  (let [nodes-changing @(refe/subscribe [:edit-nodes/nodes-changing])]
    [:div.background-1.padded-as-button {:style {:height "100%"}}
     ;; group op
     [:div.mik-flush-right
      [views-utils/icon "photo_size_select_small"]
      " Selected " [:b (:total-amount nodes-changing)] " files"
      [:br]
      [:a.unstyled-link {:href "#" :on-click #(refe/dispatch [:edit-nodes/cancel-tagging])} " Unselect"]]
     ;; group operations on dataset
     [group-operations nodes-changing]
     ;; tags to remove
     [remove-tags-from-selection nodes-changing]
     ;; tags to add
     [add-tags-to-selection nodes-changing]
     ;; changes to submit
     [:div.mik-flush-right.padded-as-button {:style {:margin-top "5em"}}
      [:a.pure-button {:href "#" :on-click #(refe/dispatch [:edit-nodes/cancel-tagging])} [views-utils/icon "cancel"] " Cancel"]
      [:a.pure-button {:href "#" :on-click #(refe/dispatch [:edit-nodes/submit-tagging])} [views-utils/icon "save"]   " Save"]]]))
