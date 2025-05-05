###########################################################################
# FILE: boogle.py
# EXERCISE: Intro2cs ex11 2022-2023
# WRITER:  Nicole Aguilar and Jana Hirbawi
# DESCRIPTION: This file is responsible for the integrating the graphics and
# the game itself
###########################################################################
from GUI import GUIGame
from functional_part import ButtonsCommand


def get_words(words_file) -> set[str]:
    """Gets the set of all valid words that the player can find"""
    with open(words_file, "r") as f:
        contents = f.read()
        words = contents.split()
    return set(words)


class Controller:
    """Is in charge of binding the
    graphic design of the GUIGame class with the functional part of the Game
    (which nows the rules of the game) """

    def __init__(self, words) -> None:
        """Initializes the class Controller"""
        self._gui = GUIGame()
        self.words = words
        self._functional_part = ButtonsCommand(self._gui, self.words)

        # Binds each button with their function
        self._gui.get_exit_f1().configure(
            command=self._functional_part.exit_game)
        self._gui.get_start_button().configure(
            command=self._functional_part.start_game)
        self._gui.get_exit_button_f3().configure(
            command=self._functional_part.exit_game)
        self._gui.get_exit_button_f2().configure(
            command=self._functional_part.exit_game)
        self._gui.get_play_again_button().configure(
            command=self._functional_part.play_again)
        self._gui.get_check_button().configure(
            command=self._functional_part.check_word)

    def run(self) -> None:
        """Runs the the fully functional game (design+rules)"""
        self._gui.run()


if __name__ == "__main__":
    words = get_words("boggle_dict.txt")
    Controller(words).run()
