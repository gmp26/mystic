(ns ^:figwheel-always mystic.components
  (:require
   [rum.core :as rum]
   [mystic.core :as core :refer [model]]
   ))

(def min-nodes 2)
(def max-nodes 25)
(def mystic-r 190)
(def mystic-o {:x 200 :y 200})

(defn dot-coord [key theta]
  (+ (key mystic-o) (* mystic-r ((if (= key :x) Math.cos Math.sin) theta))))

(defn i->theta [n i]
  (/ (* 2 Math.PI i) n))

(defn thetas [n]
  (map #(i->theta n %) (range n)))

(rum/defc dot [theta]
  [:circle {:stroke "#ffffff" :stoke-width 5 :fill "#CCCCCC" :r 4
            :cx (dot-coord :x theta)
            :cy (dot-coord :y theta)}])

(rum/defc n-slider < rum/cursored [model mystic-n]
  [:input {:type "range" :value @mystic-n :min min-nodes :max max-nodes
           :style {:width "100%"}
           :on-change #(swap! core/model assoc
                              :mystic-n (-> % .-target .-value))}])

(rum/defc t-slider < rum/static [model t]
  [:input {:type "range" :value (* 100 t) :min 0 :max 100
           :style {:width "100%"}
           :on-change #(swap! model assoc :mystic-t (/ (-> % .-target .-value) 100))
           }])

(rum/defc count-input < rum/cursored [model mystic-n]
  [:span.node-count
   [:span
    [:input.inp-default
     {:type "number"
      :min min-nodes :max max-nodes :value @mystic-n
      :on-change #(swap! model assoc :mystic-n (.parseInt js/Number (-> % .-target .-value)))}]
    "points"]
   (n-slider model (rum/cursor model [:mystic-n]))])

(rum/defc dots-on-circle < rum/cursored [mystic-n]
  [:g (map-indexed  #(rum/with-key (dot %2) %1) (thetas @mystic-n))])

(rum/defc chord [theta1 theta2 t]
  [:line {:x1 (dot-coord :x theta1)
          :y1 (dot-coord :y theta1)
          :x2 (+ (* (dot-coord :x theta1) (- 1 t)) (* (dot-coord :x theta2) t))
          :y2 (+ (* (dot-coord :y theta1) (- 1 t)) (* (dot-coord :y theta2) t))
          :stroke "#08f"
          :stroke-width 1
          :marker-end "url(#arrow)"
          }])

#_(rum/defc chords [mystic-n mystic-t method]
  (let [t (if (= method 1) (/ @mystic-t 2) @mystic-t)]
    [:g
     (for [i (range @mystic-n)
           j (range (if (= method 1) @mystic-n i))]
       (rum/with-key (chord (i->theta @mystic-n i) (i->theta @mystic-n j) t) [i j]))])
  )

(defn ramp [at width x]
  (let [a (+ at width)]
    (cond (< x at) 0
          (< x (+ at width)) (/ (- x at) width)
          :else 1
          ))
  )

(rum/defc chords [mystic-n mystic-t method]
  (let [t (if (= method 1) (/ @mystic-t 2) @mystic-t)]
    [:g
     (for [i (range @mystic-n)
           j (range (if (= method 1) @mystic-n (if (= method 4) (- @mystic-n i) (inc i))))
           ]
       (cond
         (#{1 2} method) (rum/with-key (chord (i->theta @mystic-n i) (i->theta @mystic-n j) t) [2 i j])
         (= method 3) (rum/with-key (chord (i->theta @mystic-n i) (i->theta @mystic-n j) (ramp (/ (dec i) @mystic-n) (/ 1 @mystic-n) t)) [3 i j])
         (= method 4) (rum/with-key (chord (i->theta @mystic-n i) (i->theta @mystic-n (dec (- @mystic-n j))) (ramp (/ i @mystic-n) (/ 1 @mystic-n) t)) [4 i j])
         ))])
  )

(rum/defc draw [model t-cursor]
  [:#methods
   [:div {:key 1
          :style {:margin-top "10px"}} "Draw"]
   [:div {:key 2}
    (t-slider model @t-cursor)]])

(rum/defc basic-mystic-rose < rum/cursored rum/cursored-watch [model]
  [:div
   (count-input model (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]) (rum/cursor model [:mystic-t]) 1)
     ]]
   [:p (str @model)]
])

(rum/defc mystic-rose < rum/cursored rum/cursored-watch [model method]
  [:div {:style {:display "inline-block" :width "50%"}}
   [:strong (str "Method " method)]
   [:svg {:view-box "0 0 400 400"}
    [:defs
     [:marker {:id "arrow"
               :view-box "-1 -1 2 2"
               }
      [:circle {:cx -2 :cy -2 :r 10 :fill "black"}]
      ;[:path {:d "M 0 0 L 10 5 L 0 10 z"}]
      ]]
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]) (rum/cursor model [:mystic-t]) method)
     ]]
])

(rum/defc side-by-side < rum/cursored rum/cursored-watch [model]
  [:div
   (draw model (rum/cursor model [:mystic-t]))
   (count-input model (rum/cursor model [:mystic-n]))
   [:div {:style {:clear "both"}}
    (mystic-rose model 1) (mystic-rose model 2)]
   [:div (mystic-rose model 3) (mystic-rose model 4)]]
  )
