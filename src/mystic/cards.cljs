(ns ^:figwheel-always mystic.cards

  (:require
   [rum.core :as rum]
   [mystic.core :as core]
   [mystic.components :as comp])

  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]
   ))

(enable-console-print!)

#_(defcard basic-mystic-rose
  (comp/basic-mystic-rose core/model))

(defcard two-draw-methods
  (comp/side-by-side core/model))
