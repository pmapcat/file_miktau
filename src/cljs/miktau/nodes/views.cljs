(ns miktau.nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [miktau.breadcrumbs.views :as breadcrumbs-views]
            [miktau.autocomplete.views :as autocomplete-views]            
            [re-frame.core :as refe]
            [miktau.tools :as utils]))

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defn back-button
  []
  [:div {:style {:position "relative"}}
   (if-not @(refe/subscribe [:undos?])
     [:a.unstyled-link.light-gray
      {:style
       {:font-size "5em" :cursor "default"}}
      [views-utils/icon "keyboard_arrow_left"]]
     [:a.unstyled-link.black-clickable
      {:on-click #(refe/dispatch [:undo])
       :style
       {:font-size "5em"}}
      [views-utils/icon "keyboard_arrow_left"]])])
(defn top-drawer-chroot []
  
  
  )

(defn filter-input []
  [:div.pure-g.padded-as-button
   [:div.pure-u-7-8
    [autocomplete-views/filter-input [:nodes/get-app-data] false]]
     [:div.pure-u-1-8.mik-flush-right
      [:div.pure-button.pure-button-primary  {:style {:width "80%"}}
       [:div {:on-click #(refe/dispatch [:nodes/open-file-selecting-dialog])} "Add files"]]]])

(defn radio-button
  [text on-change selected?]
  [:label.pure-checkbox
   {:for "blab" :style {:position "relative"}}
   [:input
    {:id "blab"  :style {:width "25px" :height "25px" :cursor "pointer"} :checked selected? :type "checkbox" :on-change on-change}]
   [:span {:style {:padding-bottom "5px"}}
    text]])

(defn nodes-selected-view []
  (let [nodes-selection @(refe/subscribe [:nodes/nodes-selection])]
    [:div {:style {:font-size "0.8em" }}
     ;; selected
     [:div.pure-u-1-2 
      [:h2.mik-cut-top.light-gray "Group actions on"]
      [:span "Selected: " [:b (:total nodes-selection)] " files"] [:br]
      [:div.unstyled-link.blue-clickable {:on-click #(refe/dispatch [:nodes/select-all-nodes]) :style {:cursor "pointer"}} " Select all "]]
     
     ;; node items, narrow down
     [:div.pure-u-1-2.mik-flush-right
      [:button.pure-button.pure-button-primary
       {:on-click #(refe/dispatch (get-in nodes-selection [:narrow-results :on-click]))}
       (get-in nodes-selection [:narrow-results :name])] [:br]
      
      ;; each file action available
      (for [item (:links nodes-selection)]
        ^{:key (:name item)}
        [:span
         (if (:disabled? item)
           [:a.unstyled-link.blue-disaabled (:name item)]
           [:a.unstyled-link.blue-clickable {:on-click #(refe/dispatch (:on-click item))} (:name item )])
         [:br]])]]))

(defn breadcrumbs []
  [breadcrumbs-views/breadcrumbs [:nodes/get-app-data]])

(defn- sortable-header [order-by]
  [:span
   (:name order-by) [:br]
   [:span {:style {:font-size "0.6em"}}
    "[ "
    (for [[_ point] (:items order-by)]
      [:a.unstyled-link
       {:key (:name point) :on-click #(refe/dispatch (:on-click point)) :class (if (:enabled? point) " green-disabled " " black-clickable ")} (:name point) " "]) "]"]])

(defn file-table-header [all-selected? order-by]
  (let [amount-selected @(refe/subscribe [:nodes/amount-selected])]
    [:tr
     ;; select all nodes button
     [:th.mik-flush-left]
     [:th.mik-flush-left
      [sortable-header (:name order-by)]]
     [:th.mik-flush-left.padded-as-button
      [:span
       "Showing: " [:b (:total amount-selected)  " results "] [:br]]
      [:span {:style {:font-size "0.6em"}}
       "Selected: " [:b (:amount amount-selected) ] " "
       [:a.unstyled-link.blue-clickable {:on-click #(refe/dispatch [:nodes/select-all-nodes])} " Select all "]]
      
      ]
     [:th.mik-flush-right
      [sortable-header (:modified order-by)]]]))

(defn tagging-in-a-single-node-item
  [tags]
  [:span
   (for [[tag meta-tag] (for-every-and-last tags)]
     ^{:key (:key-name tag)}
     [:span
      [:a.tag
       {:on-click #(refe/dispatch [:nodes/clicked-cloud-item (tag :key-name)])
        :class (cond
                 (:same-as-prev? tag) " almost-hidden-in-plain-sight "
                 (:selected? tag) "selected"
                 (:can-select? tag) "can-select"
                 :else "disabled")
        :style {:pointer "cursor"}}
       (:name tag)]
      [:span {:class (if  (:same-as-prev? tag) " almost-hidden-in-plain-sight " "")}
       (if-not (:last? meta-tag) "»" "")]])])

(defn single-node-item [node]
  [:tr
   {:key (str  (:id node))
    :style {:font-size "0.8em" :border-bottom "solid 1px #e3e3e3" :cursor "pointer"}
    :class (if (:selected? node) " node-selected " "")
    }
   [:td
    [:div {:style { :padding-left "10px"}}
     [radio-button "" #(refe/dispatch (:on-click  node)) (:selected? node)]]]
   [:td
    [:a.unstyled-link.black-clickable 
     {:style {:font-weight "300" :word-wrap "break-word"}}
     (:name node)]]
   [:td
    [tagging-in-a-single-node-item (:tags node)]
    (if (and  (not  (empty? (:all-tags node))) )
      [:a.unstyled-link.black-clickable
       {:class (if  (:all-tags-repeat-as-prev? node) " almost-hidden-in-plain-sight " "")
        :on-click #(refe/dispatch [:nodes/clicked-many-cloud-items (:all-tags node)])}
       [:span
        {:style {:padding "3px" :margin "3px" :font-size "0.8em"}}
        [views-utils/icon "arrow_forward"]]]
      [:span])]
   [:td.mik-flush-right
    [:a.unstyled-link.
     {:class (if (:modified-as-prev? node)
               " almost-hidden-in-plain-sight "
               "")
      :style {:font-weight "300" :color "black" :cursor "default"}}
     (node :modified)]]])
(defn pagination
  [paginator]
  [:div.mik-flush-right {:style {:font-size "0.5em"}}
   (for [page  (:pages paginator)]
     ^{:key (if (:fedots? page) "first_dots" (:name page))}
     [:div.pure-button {:on-click #(refe/dispatch (:on-click page)) :class
                        (str
                         (if-not (:active? page) " pure-button-disabled " "")
                         (if (:current? page) " pure-button-primary " ""))
                        } (:name page)])])

(defn file-table []
  (let [node-items  @(refe/subscribe [:nodes/node-items])
        order-by    @(refe/subscribe [:nodes/order-by])
        paginate    @(refe/subscribe [:nodes/pagination])]
    ;; (if selection-mode?
    ;;   [:button.pure-button
    ;;    {:on-click #(refe/dispatch [:nodes/edit-nodes])} "Edit tags"]
    ;;   [:span])
    
    
    [:table {:style {:width "100%"}}
     [:thead
      [file-table-header  (:all-selected? node-items) order-by]]
     [:tbody
      (for [node (:nodes node-items)]
        ^{:key (str (:id node))}
        [single-node-item node])]
     [:tfoot
      [:tr [:td] [:td] [:td]
       [:td
        [:div {:style {:margin-top "1em"}}
         [pagination paginate]]]]]]))

(defn main
  []
  [:div.pure-g
   [:div.pure-u-1 {:style {:box-shadow "1px 1px 2px 0px gray"}}
    [:div.pure-u-1-24
     [back-button]]
    [:div.pure-u-23-24
     [filter-input]
     [:div.padded-as-button {:style {:font-size "0.7em" :padding-bottom "1em"}}
      [breadcrumbs-views/breadcrumbs [:nodes/get-app-data]]]]]

   
   [:div.pure-u-1.padded-as-button {:style {:margin-bottom "120px"}}
    [file-table]]
   (if (:is-selected? @(refe/subscribe [:nodes/amount-selected]))
     [:div {:style {:position "fixed" :left "0px" :right "0px" :bottom "0px" :background "white" :padding-right "20px" :box-shadow "grey 0px -1px 5px 0px"}}
      [:div.pure-u-1.padded-as-button 
       [nodes-selected-view]]]
     [:span])])




