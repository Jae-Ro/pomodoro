import React from 'react';
import ReactDOM from 'react-dom';
import UserDashboard from '../UserDashboard';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("UserDashboard matches its snapshot",()=>{
    const wrapper = shallow(<Provider store={store}> <UserDashboard /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})

import {actionCreators} from '../../models/actions/projectModalActions';
// import * as types from '../../constants/ActionTypes'
import { actionTypes } from '../../models/actions/projectModalActions';


describe('UserDashboard actions', () => {
  it('should create a project', () => {
    const data = 'New project'
    const expectedAction = {
        projectData: data,
        type: actionTypes.SUBMIT_PROJECT_MODAL
    }
    expect(actionCreators.submitProjectModal(data)).toEqual(expectedAction)
  })
  it('should edit a project', () => {
    const project = { id: "123456", projectname: "Pomodoro"}
    const expectedAction = {
        projectId: project.id,
        projectname: project.projectname,
        type: actionTypes.EDIT_PROJECT_MODAL
    }
    expect(actionCreators.editProjectModal(project)).toEqual(expectedAction)
  })
  it('should delete a project', () => {
    const id = 'ProjectID of Project to be Deleted'
    const expectedAction = {
        projectId: id,
        type: actionTypes.DELETE_PROJECT_MODAL
    }
    expect(actionCreators.deleteProjectModal(id)).toEqual(expectedAction)
  })

})