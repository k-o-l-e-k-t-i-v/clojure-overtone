;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(println "bada baka")
(println (midi-connected-devices))
(source midi-connected-devices)
(source square)

(node-tree)
(def control (group "control" :head))
(def gdrums (group "gdrums" :head))
(def crack (group "crack" :before synths))
(def synths (group "synths" :after gdrums))

(definst base-pulse
  [rate 32
   outa 0
   outb 0]
  (let [s (impulse:kr rate)
        c (stepper:kr s :min 0 :max 31 :step 1)]
    (out outa s)
    (out outb c)))

(def tbus (control-bus 1))
(def cntr (control-bus 1))

(def bp (base-pulse :rate 32 :outa tbus :outb cntr))
(kill 48)

(stop)
(source demo)


(demo 100 (let [i (in:kr tbus)
                t (pulse-divider:kr i 8)
                f (env-gen (perc 0 0.002 200 -10) t 1 200 1)
                a (env-gen (perc 0 0.2 1 0) t 1 0 1)
                s (sin-osc f)
                s (* a s)
                c (env-gen (perc 0 0.042 1 20) t 0.01 0.002)
                s (comb-c s 3 c 0.9)]
            (out 0 (pan2 s))))

(demo 100 (let [i (in:kr 0 1)
                m (buf-rd:kr 1 )
                ]
               ))
