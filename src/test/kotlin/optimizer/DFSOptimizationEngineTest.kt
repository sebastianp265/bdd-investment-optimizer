package optimizer

import com.github.sebastianp265.graph.StateGraph
import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.optimizer.ComparableState
import com.github.sebastianp265.optimizer.DFSOptimizationEngine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DFSOptimizationEngineTest : FunSpec({

    test("optimize finds best state with single actions") {
        data class TestState(val depth: Int, val value: Int) : ComparableState<Int> {
            override fun value(): Int = value
        }

        data class StateAction(val name: String)

        class TestStateGraph : StateGraph<TestState, StateAction> {
            override fun isTerminal(state: TestState): Boolean = state.depth >= 3

            override fun possibleTransitions(state: TestState): List<Transition<TestState, StateAction>> {
                if (isTerminal(state)) return emptyList()
                return listOf(
                    Transition(listOf(StateAction("a")), TestState(state.depth + 1, state.value + 10)),
                    Transition(listOf(StateAction("b")), TestState(state.depth + 1, state.value + 5))
                )
            }
        }

        val (bestState, transitions) = DFSOptimizationEngine.optimize(TestStateGraph(), TestState(0, 0))

        bestState.value shouldBe 30
        transitions.size shouldBe 3
        transitions.forEach { it[0].name shouldBe "a" }
    }

    test("optimize returns initial state when terminal") {
        data class TestState(val value: Int) : ComparableState<Int> {
            override fun value(): Int = value
        }

        data class StateAction(val name: String)

        class TerminalGraph : StateGraph<TestState, StateAction> {
            override fun isTerminal(state: TestState) = true
            override fun possibleTransitions(state: TestState) = emptyList<Transition<TestState, StateAction>>()
        }

        val (bestState, transitions) = DFSOptimizationEngine.optimize(TerminalGraph(), TestState(42))

        bestState.value shouldBe 42
        transitions shouldBe emptyList()
    }

    test("explore all paths in dfs order") {
        data class TestState(val path: String, val value: Int) : ComparableState<Int> {
            override fun value(): Int = value
        }

        data class StateAction(val name: String)

        val visitedTerminalStates = mutableListOf<String>()

        class DFSTrackingGraph : StateGraph<TestState, StateAction> {
            override fun isTerminal(state: TestState): Boolean {
                if (state.path.length >= 4) {
                    visitedTerminalStates.add(state.path)
                    return true
                }
                return false
            }

            override fun possibleTransitions(state: TestState): List<Transition<TestState, StateAction>> {
                if (isTerminal(state)) return emptyList()
                return listOf(
                    Transition(
                        listOf(StateAction("L")),
                        TestState(state.path + "L", state.value + 1)
                    ),
                    Transition(
                        listOf(StateAction("R")),
                        TestState(state.path + "R", state.value + 1)
                    )
                )
            }
        }

        DFSOptimizationEngine.optimize(DFSTrackingGraph(), TestState("", 0))

        // DFS visits all 2^4 = 16 terminal states from a binary tree of depth 4
        // Order: LLLL, LLLR, LLRL, LLRR, LRLL, ... RRRR
        visitedTerminalStates.size shouldBe 16
        visitedTerminalStates[0] shouldBe "LLLL"  // DFS goes left first
        visitedTerminalStates[1] shouldBe "LLLR"  // Then backtracks and goes right
        visitedTerminalStates.last() shouldBe "RRRR"  // Last is all right
    }

})
