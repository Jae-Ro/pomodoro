import { actionTypes } from '../actions/projectModalActions';

const initialState = {
    projectModalOpen: false,
    projectTableData: [],
    selectedProject: {},
    editProject: false
};

export default (state = initialState, action) => {
    switch (action.type) {
        case actionTypes.OPEN_PROJECT_MODAL:
            return {
                projectModalOpen: true
            };
        case actionTypes.CLOSE_PROJECT_MODAL:
            return {
                projectModalOpen: false
            };
        case actionTypes.SUBMIT_PROJECT_MODAL:
            initialState.editProject = false;
            return {
                projectModalOpen: false,
                editProject: false
            };
        case actionTypes.EDIT_PROJECT_MODAL:
            initialState.selectedProject = { id: action.projectId, projectname: action.projectname };
            initialState.editProject = true;
            return {
                selectedProject: initialState.selectedProject,
                projectModalOpen: true,
                editProject: initialState.editProject
            };
        case actionTypes.DELETE_PROJECT_MODAL:
            return {
                projectModalOpen: false,
            };
        default:
            return state;
    }
};
