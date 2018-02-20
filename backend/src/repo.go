package main


var currentID int

var todos Todos

// init Give us some seed data
func init() {
	RepoCreateTodo(Todo{Name: "Write presentation"})
	RepoCreateTodo(Todo{Name: "Host meetup"})
}

// RepoFindTodo docs
func RepoFindTodo(id int) Todo {
	for _, t := range todos {
		if t.ID == id {
			return t
		}
	}
	// return empty Todo if not found
	return Todo{}
}

// RepoCreateTodo this is bad, I don't think it passes race condtions
func RepoCreateTodo(t Todo) Todo {
	currentID++
	t.ID = currentID
	todos = append(todos, t)
	return t
}
