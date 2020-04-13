const OPEN_PROJECT_MODAL = 'PTTWeb4/modal/OPEN_PROJECT_MODAL';
const CLOSE_PROJECT_MODAL = 'PTTWeb4/modal/CLOSE_PROJECT_MODAL';
const SUBMIT_PROJECT_MODAL = 'PTTWeb4/modal/SUBMIT_PROJECT_MODAL';
const EDIT_PROJECT_MODAL = 'PTTWeb4/modal/EDIT_PROJECT_MODAL';
const DELETE_PROJECT_MODAL = 'PTTWeb4/modal/DELETE_PROJECT_MODAL';

export const actionTypes = {
    OPEN_PROJECT_MODAL,
    CLOSE_PROJECT_MODAL,
    SUBMIT_PROJECT_MODAL,
    EDIT_PROJECT_MODAL,
    DELETE_PROJECT_MODAL
};
const openProjectModal = () => ({
    type: OPEN_PROJECT_MODAL
});
const closeProjectModal = () => ({
    type: CLOSE_PROJECT_MODAL
});
const submitProjectModal = (data) => ({
    projectData: data,
    type: SUBMIT_PROJECT_MODAL
});
const editProjectModal = (project) => ({
    projectId: project.id,
    projectname: project.projectname,
    type: EDIT_PROJECT_MODAL
});

const deleteProjectModal = (id) => ({
    projectId: id,
    type: DELETE_PROJECT_MODAL
});
export const actionCreators = {
    openProjectModal,
    closeProjectModal,
    submitProjectModal,
    editProjectModal,
    deleteProjectModal
};