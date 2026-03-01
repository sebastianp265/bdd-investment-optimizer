package optimizer

import com.github.sebastianp265.graph.StateGraph
import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.optimizer.ComparableState
import io.kotest.core.spec.style.FunSpec

class DFSOptimizationEngineTest : FunSpec({

    test("optimize") {
       data class TestState(val depth: Int) : ComparableState<Int> {

            override fun value(): Int {
                return 0
            }
        }

        data class StateAction(val name: String)

        val maxDepth = 3

        class TestStateGraph : StateGraph<TestState, StateAction> {

            override fun isTerminal(state: TestState): Boolean {
                return state.depth >= maxDepth
            }

            override fun possibleTransitions(state: TestState): List<Transition<TestState, StateAction>> {

            }
        }

    }
})
