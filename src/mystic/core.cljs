(ns ^:figwheel-always mystic.core)

(enable-console-print!)

(defonce model
  (atom {:mystic-n 10
         :mystic-t [0 0 0 0]
         }))

;; main entry, not yet
