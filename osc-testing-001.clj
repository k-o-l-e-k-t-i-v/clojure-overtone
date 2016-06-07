(ns osc-testing)
(use 'overtone.core)
(use 'late-night.core)
                                        ;(use 'overtone.osc)

(def PORT 4242)
(def server (osc-server PORT))
(def client (osc-client "localhost" PORT))
(osc-handle server "/beep" (fn [msg] (do (println "MSG: " msg)
                                        (test-beep))))
(osc-send client "/beep" "i" 1)
(doseq [val (range 10)]
  (osc-send client "/test" "i" val))

(osc-rm-handler server "/beep")

(def m-1 (metronome 360))
(stop)
(defn send-pattern-tick []
  (test-beep)
  (osc-send client "/beep" "i" (int (rand 100))))



(def p-1 (ref [1 0 0 0 1 0 0 0 1 0 0 0 1 0 0 0]))
(at-zero-beat m-1 8 p-1 #'send-pattern-tick)
(stop)
(send-pattern-tick)


(definst test-beep []
  (let [s (sin-osc 300)
        e (env-gen (perc 0 0.13 1 0)
                   1 1 0 1 FREE)
        o (* s e)]
    (out 0 (pan2 o))))
(test-beep)


(osc-send client "/beep" "i" 1)

(osc-listen server (fn [msg] (println "LISTENER: " msg)) :debug)
(osc-rm-listener server :debug)
(osc-rm-handler server "/beep")
(stop)

(odoc send-reply )
